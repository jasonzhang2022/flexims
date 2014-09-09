package com.flexdms.flexims.jpa.rs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileIDs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileInfos;
import com.flexdms.flexims.file.FileSchemaLiteral;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.file.FileUtils;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.DeleteOnCloseInputStream;
import com.flexdms.flexims.util.Utils;

/**
 * Manage client files (files uploaded from client) and server files
 * 
 * @author jason.zhang
 * 
 */
@Path("/file")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class FileService {

	@Context
	HttpServletRequest request;

	@Context
	Request rs;

	@Inject
	EntityManager em;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	public com.flexdms.flexims.file.FileServiceI getFileService(String schema) {
		return BeanProvider.getContextualReference(com.flexdms.flexims.file.FileServiceI.class, new FileSchemaLiteral(schema));
	}

	@Path("/upload/{schema}")
	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	public FileInfo upload(@PathParam("schema") String schema, @FormDataParam("file") InputStream file, @FormDataParam("file") FormDataBodyPart part)
			throws IOException {

		com.flexdms.flexims.file.FileServiceI fileService = getFileService(schema);
		FileInfo fileInfo = new FileInfo();
		// size is -1.

		fileInfo.setMimeType(part.getMediaType().toString());
		fileInfo.setName(part.getContentDisposition().getFileName());
		fileInfo.setTemp(true);

		if (part.getContentDisposition().getSize() == -1) {
			java.nio.file.Path tempFile = Files.createTempFile("fx", null);
			Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);
			// DELETE_ON_CLOSE is not supported
			// InputStream inputStream=Files.newInputStream(tempFile,
			// StandardOpenOption.DELETE_ON_CLOSE);
			InputStream inputStream = new DeleteOnCloseInputStream(tempFile);
			fileInfo.setSize(Files.size(tempFile));
			// do not close inputStream, implementation may transfer file
			// asynchronously.
			return fileService.saveFile(inputStream, fileInfo);
		} else {
			fileInfo.setSize(part.getContentDisposition().getSize());
			return fileService.saveFile(file, fileInfo);
		}

	}

	@Path("/topermanent/{schema}/{id}")
	@GET
	public AppMsg toPerm(@PathParam("schema") String schema, @PathParam("id") String id) {
		FileID fileID = new FileID(schema, id);
		FileServiceI fileService = getFileService(fileID.getSchema());
		fileService.changeStatus(fileID, false, null, null);

		return new AppMsg();
	}

	@Path("/totemp/{schema}/{id}")
	@GET
	public AppMsg toTemp(@PathParam("schema") String schema, @PathParam("id") String id) {
		FileID fileID = new FileID(schema, id);
		FileServiceI fileService = getFileService(fileID.getSchema());
		fileService.changeStatus(fileID, true, null, null);
		return new AppMsg();
	}

	@Path("/fileinfo/{schema}/{id}")
	@GET
	public FileInfo getFileInfo(@PathParam("schema") String schema, @PathParam("id") String id) {
		FileID fileID = new FileID(schema, id);
		com.flexdms.flexims.file.FileServiceI fileService = getFileService(fileID.getSchema());
		return fileService.getFileInfo(fileID);
	}

	@Path("/fileinfos")
	@POST
	public FileInfos getFileInfo(FileIDs ids) {
		FileInfos infos = new FileInfos();
		for (FileID id : ids.getFileIDs()) {
			com.flexdms.flexims.file.FileServiceI fileService = getFileService(id.getSchema());
			infos.getFileInfos().add(fileService.getFileInfo(id));
		}

		return infos;
	}

	@Path("/clientfile/{schema}/{id}")
	@GET
	public Response downloadClientFile(@PathParam("schema") String schema, @PathParam("id") String id,
			@QueryParam("download") @DefaultValue("false") boolean download) {
		final FileID fileID = new FileID(schema, id);
		final com.flexdms.flexims.file.FileServiceI fileService = getFileService(fileID.getSchema());
		FileInfo fileInfo = fileService.getFileInfo(fileID);
		if (fileInfo.getInstType() != null) {
			// if we can read instance, we have download permission.
			em.find(JpaHelper.getEntityClass(em, fileInfo.getInstType()), Long.parseLong(fileInfo.getInstId()));
		}
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					InputStream inputStream = fileService.getContent(fileID);
					IOUtils.copy(inputStream, output);
					inputStream.close();
				} catch (IOException e) {
					throw new WebApplicationException(e);
				}

			}
		};

		ResponseBuilder builder = Response.ok(stream).type(fileInfo.getMimeType()).header("Content-Length", fileInfo.getSize());
		if (download) {
			builder.header("Content-Disposition", "attachment;filename=" + fileInfo.getName());
		} else {
			builder.header("Content-Disposition", "inline;filename=" + fileInfo.getName());
		}
		return builder.build();
	}

	@Path("/clientfiles")
	@POST
	public Response downloadClientFile(final FileIDs ids) {
		final com.flexdms.flexims.file.FileServiceI fileService = getFileService(ids.getFileIDs().get(0).getSchema());
		final FileInfos infos = new FileInfos();

		for (FileID id : ids.getFileIDs()) {
			FileInfo fileInfo = fileService.getFileInfo(id);
			if (fileInfo.getInstType() != null) {
				// if we can read instance, we have download permission.
				em.find(JpaHelper.getEntityClass(em, fileInfo.getInstType()), Long.parseLong(fileInfo.getInstId()));
			}
			fileInfo.setFileID(id); // need the prefix
			infos.getFileInfos().add(fileInfo);
		}

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					ZipOutputStream zip = new ZipOutputStream(output);
					for (FileInfo fileInfo : infos.getFileInfos()) {

						ZipEntry zipEntry = new ZipEntry(fileInfo.getFileID().getPropName() + "/" + fileInfo.getName());
						zipEntry.setSize(fileInfo.getSize());
						zip.putNextEntry(zipEntry);
						InputStream inputStream = fileService.getContent(fileInfo.getFileID());
						IOUtils.copy(inputStream, zip);
						inputStream.close();
						zip.closeEntry();
					}
					zip.close();
				} catch (IOException e) {
					throw new WebApplicationException(e);
				}

			}
		};
		ResponseBuilder builder = Response.ok(stream).type("application/zip").header("Content-Disposition", "attachment;filename=batch.zip");
		return builder.build();
	}

	@Path("/clientfiles/{type}/{id: \\d+}")
	@GET
	public Response downloadClientFiles(@PathParam("type") String type, @PathParam("id") long id) {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), id);
		FileIDs fileIDs = FileUtils.searchFileIDs(inst, null, null);
		return downloadClientFile(fileIDs);
	}

	@Path("/clientfiles/{type}/{id: \\d+}/{propname}")
	@GET
	public Response downloadClientFiles(@PathParam("type") String type, @PathParam("id") long id, @PathParam("propname") String propName) {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), id);
		FileIDs fileIDs = FileUtils.searchFileIDs(inst, propName, null);
		return downloadClientFile(fileIDs);
	}

	public boolean multipleFiles(FileInfos infos) {
		if (infos.getFileInfos().size() > 1) {
			return true;
		}
		java.nio.file.Path filePath = FileUtils.serverFileTopDir().resolve(infos.getFileInfos().get(0).getName());
		if (Files.isDirectory(filePath)) {
			return true;
		}
		return false;
	}

	public Response downloadPath(final java.nio.file.Path path) {
		if (Files.isDirectory(path)) {
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException {
					try {

						Utils.zipFolder(path.toFile(), output);
					} catch (IOException e) {
						throw new WebApplicationException(e);
					}

				}
			};
			ResponseBuilder builder = Response.ok(stream).type("application/zip")
					.header("Content-Disposition", "attachment;filename=" + path.getFileName().toString() + ".zip");
			return builder.build();
		}

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					Files.copy(path, output);
				} catch (IOException e) {
					throw new WebApplicationException(e);
				}

			}
		};
		String mimeType = null;
		try {
			mimeType = Files.probeContentType(path);
		} catch (IOException e) {
		}
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		ResponseBuilder builder = Response.ok(stream).type(mimeType)
				.header("Content-Disposition", "inline;filename=" + path.getFileName().toString());
		return builder.build();
	}

	public Response downloadServerFile(FileInfos infos1, String zipname) {

		final List<FileInfo> infos = infos1.getFileInfos();

		if (infos.size() > 1 || (!infos.isEmpty() && infos.get(0).isDir())) {
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException {
					try {

						ZipOutputStream zip = new ZipOutputStream(output);
						for (FileInfo fileInfo : infos) {
							java.nio.file.Path filePath = FileUtils.serverFileTopDir().resolve(fileInfo.getName());
							if (Files.isDirectory(filePath)) {
								Utils.addFolderToZip(filePath.toFile(), zip, FileUtils.serverFileTopDir().toString() + File.separator);
							} else {
								Utils.addFileToZip(filePath.toFile(), zip, FileUtils.serverFileTopDir().toString() + File.separator);
							}
						}
						zip.close();
					} catch (IOException e) {
						throw new WebApplicationException(e);
					}

				}
			};
			ResponseBuilder builder = Response.ok(stream).type("application/zip").header("Content-Disposition", "attachment;filename=" + zipname);
			return builder.build();
		}

		FileInfo fileInfo = infos.iterator().next();
		final java.nio.file.Path filePath = FileUtils.serverFileTopDir().resolve(fileInfo.getName());
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					Files.copy(filePath, output);
				} catch (IOException e) {
					throw new WebApplicationException(e);
				}

			}
		};
		ResponseBuilder builder = Response.ok(stream).type(fileInfo.getMimeType())
				.header("Content-Disposition", "inline;filename=" + filePath.getFileName().toString());
		return builder.build();
	}

	@Path("/serverfiles/{type}/{id: \\d+}")
	@GET
	public Response downloadServerFiles(@PathParam("type") String type, @PathParam("id") long id) {
		FileInfos fileInfos = listServerFiles(type, id);
		return downloadServerFile(fileInfos, type + id + ".zip");
	}

	@Path("/serverfiles/{type}/{id: \\d+}/{propname}")
	@GET
	public Response downloadServerFiles(@PathParam("type") String type, @PathParam("id") long id, @PathParam("propname") String propName) {
		FileInfos fileInfos = listServerFiles(type, id, propName);
		return downloadServerFile(fileInfos, propName + ".zip");
	}

	@Path("/serverfiles/{type}/{id: \\d+}/{propname}/{dirprefix: .+}")
	@GET
	public Response downloadServerFiles(@PathParam("type") String type, @PathParam("id") long id, @PathParam("propname") String propName,
			@PathParam("dirprefix") String dir) {
		java.nio.file.Path path = FileUtils.serverFileTopDir().resolve(dir);
		FileInfos fileInfos = listServerFiles(type, id, propName);
		FileInfo fileInfo1 = null;
		for (FileInfo f : fileInfos.getFileInfos()) {
			java.nio.file.Path fPath = FileUtils.serverFileTopDir().resolve(f.getName());
			if (path.startsWith(fPath)) {
				fileInfo1 = f;
				break;
			}
		}
		if (fileInfo1 == null || !fileInfo1.isDir()) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		return downloadPath(path);
	}

	@Path("/listserverfiles/{type}/{id: \\d+}")
	@GET
	public FileInfos listServerFiles(@PathParam("type") String type, @PathParam("id") long id) {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), id);
		FileInfos fileInfos = FileUtils.searchServerFileInfos(inst, null, null, null);
		Collections.sort(fileInfos.getFileInfos(), new Comparator<FileInfo>() {

			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		return fileInfos;
	}

	@Path("/listserverfiles/{type}/{id: \\d+}/{propname}")
	@GET
	public FileInfos listServerFiles(@PathParam("type") String type, @PathParam("id") long id, @PathParam("propname") String propName) {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), id);
		FileInfos fileInfos = FileUtils.searchServerFileInfos(inst, propName, null, null);
		Collections.sort(fileInfos.getFileInfos(), new Comparator<FileInfo>() {

			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		return fileInfos;
	}

	/**
	 * 
	 * @param type
	 * @param id
	 * @param propName
	 * @param dir
	 *            an directory under control of the particular propname.
	 * @return
	 */
	@Path("/listserverfiles/{type}/{id: \\d+}/{propname}/{dirprefix: .+}")
	@GET
	public FileInfos listServerFiles(@PathParam("type") String type, @PathParam("id") long id, @PathParam("propname") String propName,
			@PathParam("dirprefix") String dir) {
		java.nio.file.Path path = FileUtils.serverFileTopDir().resolve(dir);
		FileInfos fileInfos = listServerFiles(type, id, propName);
		FileInfo fileInfo1 = null;
		for (FileInfo f : fileInfos.getFileInfos()) {
			java.nio.file.Path fPath = FileUtils.serverFileTopDir().resolve(f.getName());
			if (path.startsWith(fPath)) {
				fileInfo1 = f;
				break;
			}
		}
		if (fileInfo1 == null || !fileInfo1.isDir()) {
			fileInfos.getFileInfos().clear();
			return fileInfos;
		}
		return FileUtils.collectChildFileInfos(path, FileUtils.serverFileTopDir(), false);
	}

}
