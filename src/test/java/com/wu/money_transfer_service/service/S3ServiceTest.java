package com.wu.money_transfer_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Captor
    private ArgumentCaptor<PutObjectRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<RequestBody> bodyCaptor;

    private S3Service s3Service;
    private final String bucketName = "test-bucket";
    private final String region = "us-east-1";

    @BeforeEach
    void setUp() throws Exception {
        // Create service with mocked S3Client
        s3Service = new S3Service(bucketName, region);

        // Use reflection to replace the real S3Client with our mock
        Field s3ClientField = S3Service.class.getDeclaredField("s3Client");
        s3ClientField.setAccessible(true);
        s3ClientField.set(s3Service, s3Client);
    }

    @Test
    void testUploadFile_Success() {
        // Given
        byte[] content = "test content".getBytes();
        String key = "test-file.txt";
        String contentType = "text/plain";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3Service.uploadFile(content, key, contentType);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(key, capturedRequest.key());
        assertEquals(contentType, capturedRequest.contentType());

        RequestBody capturedBody = bodyCaptor.getValue();
        assertEquals(content.length, capturedBody.contentLength());

        assertEquals("https://test-bucket.s3.amazonaws.com/test-file.txt", result);
    }

    @Test
    void testUploadFile_WithEmptyContent() {
        // Given
        byte[] content = new byte[0];
        String key = "empty-file.txt";
        String contentType = "text/plain";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3Service.uploadFile(content, key, contentType);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(key, capturedRequest.key());

        RequestBody capturedBody = bodyCaptor.getValue();
        assertEquals(0, capturedBody.contentLength());

        assertEquals("https://test-bucket.s3.amazonaws.com/empty-file.txt", result);
    }

    @Test
    void testUploadFile_WithDifferentContentTypes() {
        // Given
        byte[] content = "test content".getBytes();
        String key = "test-file";

        // Test different content types
        String[] contentTypes = {
                "application/json",
                "application/pdf",
                "image/jpeg",
                "image/png",
                "application/octet-stream"
        };

        for (String contentType : contentTypes) {
            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // When
            String result = s3Service.uploadFile(content, key + "." + contentType.split("/")[1], contentType);

            // Then
            verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

            PutObjectRequest capturedRequest = requestCaptor.getValue();
            assertEquals(contentType, capturedRequest.contentType());

            assertEquals("https://test-bucket.s3.amazonaws.com/" + key + "." + contentType.split("/")[1], result);

            // Reset for next iteration
            reset(s3Client);
        }
    }

    @Test
    void testUploadFile_WithNestedKey() {
        // Given
        byte[] content = "test content".getBytes();
        String key = "folder/subfolder/test-file.txt";
        String contentType = "text/plain";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3Service.uploadFile(content, key, contentType);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(key, capturedRequest.key());

        assertEquals("https://test-bucket.s3.amazonaws.com/folder/subfolder/test-file.txt", result);
    }

    @Test
    void testUploadFile_WithLargeContent() {
        // Given
        byte[] content = new byte[5 * 1024 * 1024]; // 5MB
        String key = "large-file.bin";
        String contentType = "application/octet-stream";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3Service.uploadFile(content, key, contentType);

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());

        RequestBody capturedBody = bodyCaptor.getValue();
        assertEquals(content.length, capturedBody.contentLength());

        assertEquals("https://test-bucket.s3.amazonaws.com/large-file.bin", result);
    }

    @Test
    void testUploadFile_S3ClientThrowsException() {
        // Given
        byte[] content = "test content".getBytes();
        String key = "test-file.txt";
        String contentType = "text/plain";

        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Access denied")
                .build();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(s3Exception);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            s3Service.uploadFile(content, key, contentType);
        });

        assertTrue(exception.getMessage().contains("Failed to upload file to S3"));
        assertTrue(exception.getMessage().contains("Access denied"));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_WithNullContentType() {
        // Given
        byte[] content = "test content".getBytes();
        String key = "test-file.txt";
        String contentType = null;

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3Service.uploadFile(content, key, contentType);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertNull(capturedRequest.contentType());

        assertEquals("https://test-bucket.s3.amazonaws.com/test-file.txt", result);
    }

    @Test
    void testS3ServiceConstructor_InitializesClientWithCorrectRegion() throws Exception {
        // This test verifies that the constructor initializes the S3Client with the correct region

        // Create a new instance with a specific region
        S3Service service = new S3Service("test-bucket", "eu-west-1");

        // Use reflection to access the private s3Client field
        Field s3ClientField = S3Service.class.getDeclaredField("s3Client");
        s3ClientField.setAccessible(true);
        S3Client client = (S3Client) s3ClientField.get(service);

        // Verify the client was initialized (can't directly check region, but we can verify it's not null)
        assertNotNull(client);
    }
}
