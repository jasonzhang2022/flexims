/**
 * <p>Handle client file associated with instance. A client file is handled by a {@link com.flexdms.flexims.file.FileServiceI}. 
 * Each File is identified by {@link com.flexdms.flexims.file.FileID}. Each FileServiceI implementation has a file Schema.</p> 
 * 
 * <p>When file is first uploaded or deleted from UI, it is marked as temporary. When instance is saved, its associated files 
 * are changed from temporary to peristent. A background process will remove all temporary files not modified in last 2 hours and not
 * associated with any instances</p>   
 * @author jason.zhang
 *
 */
package com.flexdms.flexims.file;