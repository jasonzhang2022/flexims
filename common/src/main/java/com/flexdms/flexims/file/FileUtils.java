package com.flexdms.flexims.file;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.EnumSet;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.util.Utils;

public final class FileUtils {

	public static final int FILE_INDEX = 17;
	public static final int DIRECTORY_INDEX = 16;

	private FileUtils() {

	}

	public static Path serverFileTopDir() {
		return Paths.get(Configs.getConfig("file.server.topdirectory"));
	}

	public static boolean isFileUpload(MappingAccessor accessor) {
		String typeidx = null;
		String clientfile = null;
		for (PropertyMetadata propmeta : accessor.getProperties()) {
			if (propmeta.getName().equalsIgnoreCase("typeidx")) {
				typeidx = propmeta.getValue();
			} else if (propmeta.getName().equalsIgnoreCase("clientfile")) {
				clientfile = propmeta.getValue();
			}
		}
		if (typeidx == null || clientfile == null) {
			return false;
		}
		if (Integer.parseInt(typeidx) != FILE_INDEX) {
			return false;
		}
		if (!Utils.stringToBoolean(clientfile, false)) {
			return false;
		}
		return true;
	}

	public static boolean isServerFile(MappingAccessor accessor) {
		String typeidx = null;
		String clientfile = null;
		for (PropertyMetadata propmeta : accessor.getProperties()) {
			if (propmeta.getName().equalsIgnoreCase("typeidx")) {
				typeidx = propmeta.getValue();
			} else if (propmeta.getName().equalsIgnoreCase("clientfile")) {
				clientfile = propmeta.getValue();
			}
		}
		if (typeidx == null || clientfile != null) {
			return false;
		}
		if (Integer.parseInt(typeidx) == FILE_INDEX || Integer.parseInt(typeidx) == DIRECTORY_INDEX) {
			return true;
		}

		return false;
	}

	public static FileInfo pathToFileInfo(Path filePath, Path start) {
		FileInfo fileInfo = new FileInfo();
		if (!Files.exists(filePath)) {
			fileInfo.setName("Unknown");
			return fileInfo;
		}

		fileInfo.setName(start.relativize(filePath).toString());

		if (Files.isDirectory(filePath)) {
			fileInfo.setDir(true);
		} else {
			fileInfo.setDir(false);
			try {
				fileInfo.setSize(Files.size(filePath));
			} catch (IOException e) {
				fileInfo.setSize(0);
			}
			try {
				fileInfo.setMimeType(Files.probeContentType(filePath));
			} catch (IOException e) {
				fileInfo.setMimeType("application/octet-stream");
			}
			if (fileInfo.getMimeType() == null) {
				fileInfo.setMimeType("application/octet-stream");
			}
		}

		return fileInfo;

	}

	public static FileInfos collectChildFileInfos(final Path filePath, final Path start, boolean recursive) {
		final FileInfos fileInfos = new FileInfos();
		int depth = recursive ? Integer.MAX_VALUE : 1;
		try {
			Files.walkFileTree(filePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), depth, new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					fileInfos.getFileInfos().add(pathToFileInfo(file, start));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.SKIP_SUBTREE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {

		}
		return fileInfos;
	}

	@SuppressWarnings("rawtypes")
	public static FileInfos searchServerFileInfos(FleximsDynamicEntityImpl de, String propName, Path prefix, Path start) {
		FileInfos fileInfos = new FileInfos();
		if (prefix == null) {
			prefix = serverFileTopDir();
		}
		if (start == null) {
			start = serverFileTopDir();
		}
		XMLEntityMappings mappings = JpaMapHelper.getInternaEntityMappings();
		Class<?> clz = de.getClass();
		String firstName = null;
		String subName = null;
		if (propName != null) {
			int i = propName.indexOf('.');
			if (i != -1) {
				firstName = propName.substring(0, i);
				subName = propName.substring(i + 1);
			} else {
				firstName = propName;
			}
		}

		while (clz != FleximsDynamicEntityImpl.class && clz != Object.class) {
			ClassAccessor accessor = JpaMapHelper.findClassAccessor(mappings, clz.getSimpleName());
			if (accessor != null) {

				// single file.
				for (BasicAccessor prop : accessor.getAttributes().getBasics()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					Object value = de.get(prop.getName());
					if (value == null) {
						continue;
					}
					if (isServerFile(prop)) {
						Path filePath = prefix.resolve((String) value);
						if (!filePath.startsWith(prefix)) {
							// you can not navigate out.
							continue;
						}
						FileInfo fileInfo = pathToFileInfo(filePath, start);
						fileInfos.getFileInfos().add(fileInfo);
					}
				}

				for (ElementCollectionAccessor prop : accessor.getAttributes().getElementCollections()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					Collection values = de.get(prop.getName());
					if (values == null || values.isEmpty()) {
						continue;
					}
					if (isServerFile(prop)) {
						for (Object value : values) {
							Path filePath = prefix.resolve((String) value);
							if (!filePath.startsWith(prefix)) {
								// you can not navigate out.
								continue;
							}
							FileInfo fileInfo = pathToFileInfo(filePath, start);
							fileInfos.getFileInfos().add(fileInfo);
						}
						continue;
					}

					Object firstObject = values.iterator().next();
					// collection of embedded
					if (firstObject instanceof FleximsDynamicEntityImpl) {
						for (Object obj : values) {
							FleximsDynamicEntityImpl embeded = (FleximsDynamicEntityImpl) obj;
							FileInfos fileInfos2 = searchServerFileInfos(embeded, subName, prefix, start);
							fileInfos.getFileInfos().addAll(fileInfos2.getFileInfos());
						}
					}
				}
				for (EmbeddedAccessor prop : accessor.getAttributes().getEmbeddeds()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					FleximsDynamicEntityImpl embeded = de.get(prop.getName());
					if (embeded == null) {
						continue;
					}
					if (embeded != null) {
						FileInfos fileInfos2 = searchServerFileInfos(embeded, subName, prefix, start);
						fileInfos.getFileInfos().addAll(fileInfos2.getFileInfos());
					}
				}
			}
			clz = clz.getSuperclass();
		}
		return fileInfos;
	}

	@SuppressWarnings("rawtypes")
	public static FileIDs searchFileIDs(FleximsDynamicEntityImpl de, String propName, String prefix) {
		FileIDs fileIDs = new FileIDs();
		XMLEntityMappings mappings = JpaMapHelper.getInternaEntityMappings();
		Class<?> clz = de.getClass();
		String firstName = null;
		String subName = null;
		if (propName != null) {
			int i = propName.indexOf('.');
			if (i != -1) {
				firstName = propName.substring(0, i);
				subName = propName.substring(i + 1);
			} else {
				firstName = propName;
			}
		}

		while (clz != FleximsDynamicEntityImpl.class && clz != Object.class) {
			ClassAccessor accessor = JpaMapHelper.findClassAccessor(mappings, clz.getSimpleName());
			if (accessor != null && accessor.getAttributes() != null) {

				// single file.
				for (BasicAccessor prop : accessor.getAttributes().getBasics()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					Object value = de.get(prop.getName());
					if (value == null) {
						continue;
					}
					if (isFileUpload(prop)) {
						FileID fileID = FileID.valueOf((String) value);

						if (fileID != null) {
							fileIDs.getFileIDs().add(fileID);
							fileID.setPropName(prefix == null ? prop.getName() : prefix + "/" + prop.getName());
						}
					}

				}

				for (ElementCollectionAccessor prop : accessor.getAttributes().getElementCollections()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					Collection values = de.get(prop.getName());
					if (values == null || values.isEmpty()) {
						continue;
					}
					if (isFileUpload(prop)) {
						for (Object value : values) {
							FileID fileID = FileID.valueOf((String) value);
							if (fileID != null) {
								fileIDs.getFileIDs().add(fileID);
								fileID.setPropName(prefix == null ? prop.getName() : prefix + "/" + prop.getName());
							}
						}
						continue;
					}
					Object firstObject = values.iterator().next();
					// collection of embedded
					if (firstObject instanceof FleximsDynamicEntityImpl) {
						for (Object obj : values) {
							FleximsDynamicEntityImpl embeded = (FleximsDynamicEntityImpl) obj;
							FileIDs iDs = searchFileIDs(embeded, subName, prefix == null ? prop.getName() : prefix + "/" + prop.getName());
							fileIDs.getFileIDs().addAll(iDs.getFileIDs());
						}
					}
				}
				for (EmbeddedAccessor prop : accessor.getAttributes().getEmbeddeds()) {
					if (firstName != null && !prop.getName().equals(firstName)) {
						continue;
					}
					FleximsDynamicEntityImpl embeded = de.get(prop.getName());
					if (embeded == null) {
						continue;
					}
					if (embeded != null) {
						FileIDs iDs = searchFileIDs(embeded, subName, prefix == null ? prop.getName() : prefix + "/" + prop.getName());
						fileIDs.getFileIDs().addAll(iDs.getFileIDs());
					}
				}
			}
			clz = clz.getSuperclass();
		}
		return fileIDs;
	}
}
