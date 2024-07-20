package com.hako.book.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hako.book.book.Book;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class fileStorageService {

	@Value("${application.file.uploads.photos-output-path}")
	private String fileUploadPath;

  public String saveFile(
   @Nonnull MultipartFile sourceFile,
   @Nonnull Integer userId
  ) {

		final String fileUploadSubPath = "users" + File.separator + userId;

		return uploadFile(sourceFile, fileUploadSubPath);
  }

	private String uploadFile(@Nonnull MultipartFile sourceFile, @Nonnull  String fileUploadSubPath) {
		final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;

		File targetFolder = new File(finalUploadPath);
		if (!targetFolder.exists()) {
			boolean folderCreated  = targetFolder.mkdirs();
			if (!folderCreated) {
				log.warn("Failed to create folder {}", finalUploadPath);
				return null;
			}
		}

		final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
		String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
		Path targetPath = Paths.get(targetFilePath);
		try {
			Files.write(targetPath, sourceFile.getBytes());
			log.info("File saved at {}", targetFilePath);
			return targetFilePath;
		} catch (IOException e) {
			log.error("Failed to save file {}", targetFilePath, e);
		}
		return null;
	}

	private String getFileExtension(String originalFilename) {
		if(originalFilename == null || originalFilename.isEmpty()) {
			return "";
		}

		int lastDotIndex = originalFilename.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return "";
		}

		return originalFilename.substring(lastDotIndex + 1).toLowerCase();
	}
  
}
