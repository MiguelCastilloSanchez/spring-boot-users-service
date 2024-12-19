package com.example.user_service.services;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

@Component
public class AzureBlobService {

   @Autowired
   BlobServiceClient blobServiceClient;

   @Autowired
   BlobContainerClient blobContainerClient;

   public void upload(MultipartFile multipartFile, String url, String containerURL) 
        throws IOException {

      // Todo UUID
      BlobClient blob = blobContainerClient
            .getBlobClient(extractBlobName(url, containerURL));
      blob.upload(multipartFile.getInputStream(),
            multipartFile.getSize(), true);
        
   }

   public void deleteBlob(String url, String containerURL) {

      BlobClient blob = blobContainerClient.getBlobClient(extractBlobName(url, containerURL));
      blob.delete();
   
   }

   private String extractBlobName(String url, String containerURL) {
      return url.substring(containerURL.length());
  }

}
