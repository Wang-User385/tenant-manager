package com.hand.hls.plugin.jacob.beans;

import java.sql.Blob;

public class AttachmentInfo
{
    private long attachmentId;
    private String fileName;
    private String mimeType;
    private int fileSize;
    private String filePath;
    private Blob content;
    private int ownerId;

    public AttachmentInfo(Long attachmentId, String fileName, String mimeType, int fileSize, String filePath, Blob content, int ownerId) {
        this.attachmentId = attachmentId.longValue();
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.content = content;
        this.ownerId = ownerId;
    }






    public Long getAttachmentId() { return Long.valueOf(this.attachmentId); }



    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId.longValue(); }



    public String getFileName() { return this.fileName; }



    public void setFileName(String fileName) { this.fileName = fileName; }



    public String getMimeType() { return this.mimeType; }



    public void setMimeType(String mimeType) { this.mimeType = mimeType; }



    public int getFileSize() { return this.fileSize; }



    public void setFileSize(int fileSize) { this.fileSize = fileSize; }



    public String getFilePath() { return this.filePath; }



    public void setFilePath(String filePath) { this.filePath = filePath; }



    public Blob getContent() { return this.content; }



    public void setContent(Blob content) { this.content = content; }



    public int getOwnerId() { return this.ownerId; }



    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }



    public void setAttachmentId(long attachmentId) { this.attachmentId = attachmentId; }
}
