package tw.school.rental_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, String path);
}
