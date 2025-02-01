package greencity.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import greencity.dto.event.ImageRequestDto;
import greencity.entity.Image;
import greencity.repository.ImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AzureImageStorageClient implements ImageStorageClient{
    private final BlobServiceClient blobServiceClient;
    private final ImageRepo imageRepo;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_FORMATES = {"jpg", "png"};


    public AzureImageStorageClient(@Autowired PropertyResolver propertyResolver, ImageRepo imageRepo) {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(propertyResolver.getProperty("azure.connection.string")).buildClient();
        this.imageRepo = imageRepo;
    }

    @Override
    public List<String> uploadImage(String containerName, List<MultipartFile> files, ImageRequestDto chosenOfProposedImage) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            if (chosenOfProposedImage != null) {
                return List.of(chosenOfProposedImage.getImagePath());
            } else {
                Image defaultImage = imageRepo.findById(1L).orElseThrow(() -> new IllegalArgumentException("Default image not found"));
                return List.of(defaultImage.getImagePath());
            }
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds the 10 MB limit.");
            }

            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

            if (!Arrays.asList(ALLOWED_FORMATES).contains(fileExtension)) {
                throw new IllegalArgumentException("Invalid file format. Allowed formats: jpg, png.");
            }

            String newImageName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));

            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(newImageName);

            InputStream fileInputStream = file.getInputStream();
            blobClient.upload(fileInputStream, file.getSize(), true);

            String contentType = Files.probeContentType(Path.of(file.getOriginalFilename()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
            blobClient.setHttpHeaders(headers);

            Image image = Image.builder()
                    .imagePath(blobClient.getBlobUrl())
                    .build();
            imageRepo.save(image);

            uploadedImageUrls.add(blobClient.getBlobUrl());
        }
        return uploadedImageUrls;
    }
}
