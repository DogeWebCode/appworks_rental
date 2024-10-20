package tw.school.rental_backend.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tw.school.rental_backend.service.StorageService;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    private final AmazonS3 amazonS3;

    public StorageServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file, String path) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3.putObject("sideproject-rental-images", path + fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
        return fileName;
    }
}
