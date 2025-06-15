package com.gmail.merikbest2015.ecommerce.service.Impl;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.dto.perfume.PerfumeSearchRequest;
import com.gmail.merikbest2015.ecommerce.enums.SearchPerfume;
import com.gmail.merikbest2015.ecommerce.exception.ApiRequestException;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.repository.projection.PerfumeProjection;
import com.gmail.merikbest2015.ecommerce.service.PerfumeService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.PERFUME_NOT_FOUND;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private static final Logger logger = LoggerFactory.getLogger(PerfumeServiceImpl.class);

    private final PerfumeRepository perfumeRepository;
    private final S3Client s3Client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;

    @Override
    public Perfume getPerfumeById(Long perfumeId) {
        logger.info("Fetching perfume with id {}", perfumeId);
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> {
                    logger.warn("Perfume not found with id (perfume by id) {}", perfumeId);
                    return new ApiRequestException(PERFUME_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public Page<PerfumeProjection> getAllPerfumes(Pageable pageable) {
        logger.info("Fetching all perfumes with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return perfumeRepository.findAllByOrderByIdAsc(pageable);
    }

    @Override
    public List<PerfumeProjection> getPerfumesByIds(List<Long> perfumesId) {
        logger.info("Fetching perfumes by ids {}", perfumesId);
        return perfumeRepository.getPerfumesByIds(perfumesId);
    }

    @Override
    public Page<PerfumeProjection> findPerfumesByFilterParams(PerfumeSearchRequest filter, Pageable pageable) {
        logger.info("Finding perfumes by filter params: perfumers={}, genders={}, price range=({}, {}), sortByPrice={}",
                filter.getPerfumers(), filter.getGenders(), filter.getPrices().get(0), filter.getPrices().get(1), filter.getSortByPrice());
        return perfumeRepository.findPerfumesByFilterParams(
                filter.getPerfumers(),
                filter.getGenders(),
                filter.getPrices().get(0),
                filter.getPrices().get(1),
                filter.getSortByPrice(),
                pageable);
    }

    @Override
    public List<Perfume> findByPerfumer(String perfumer) {
        logger.info("Finding perfumes by perfumer: {}", perfumer);
        return perfumeRepository.findByPerfumerOrderByPriceDesc(perfumer);
    }

    @Override
    public List<Perfume> findByPerfumeGender(String perfumeGender) {
        logger.info("Finding perfumes by gender: {}", perfumeGender);
        return perfumeRepository.findByPerfumeGenderOrderByPriceDesc(perfumeGender);
    }

    @Override
    public Page<PerfumeProjection> findByInputText(SearchPerfume searchType, String text, Pageable pageable) {
        logger.info("Finding perfumes by input text: type={}, text={}", searchType, text);
        if (searchType.equals(SearchPerfume.BRAND)) {
            return perfumeRepository.findByPerfumer(text, pageable);
        } else if (searchType.equals(SearchPerfume.PERFUME_TITLE)) {
            return perfumeRepository.findByPerfumeTitle(text, pageable);
        } else {
            return perfumeRepository.findByManufacturerCountry(text, pageable);
        }
    }

    @Override
    @Transactional
    public Perfume savePerfume(Perfume perfume, MultipartFile multipartFile) {
        logger.info("Saving perfume: {}", perfume.getPerfumeTitle());

        if (multipartFile == null || multipartFile.isEmpty()) {
            logger.info("No file uploaded or file is empty, setting default image");
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key("empty.jpg")
                    .build();
            perfume.setFilename(s3Client.utilities().getUrl(getUrlRequest).toString());
        } else {
            String originalFilename = multipartFile.getOriginalFilename();
            String sanitizedFilename = (originalFilename == null) ? "unknown_file" : new File(originalFilename).getName();

            File tempFile = null;
            try {
                tempFile = File.createTempFile("upload-", "-" + sanitizedFilename);
                Files.copy(multipartFile.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String fileName = UUID.randomUUID() + "." + sanitizedFilename;
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

                GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                perfume.setFilename(s3Client.utilities().getUrl(getUrlRequest).toString());
                logger.info("Uploaded file saved to S3 with filename {}", fileName);

            } catch (IOException e) {
                logger.error("Error saving multipart file", e);
                throw new RuntimeException("Error processing uploaded file", e);
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    if (!tempFile.delete()) {
                        logger.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                    }
                }
            }
        }

        Perfume savedPerfume = perfumeRepository.save(perfume);
        logger.info("Perfume saved with id {}", savedPerfume.getId());
        return savedPerfume;
    }

    @Override
    @Transactional
    public String deletePerfume(Long perfumeId) {
        logger.info("Deleting perfume with id {}", perfumeId);
        Perfume perfume = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> {
                    logger.warn("Perfume not found with id (delete perfume) {}", perfumeId);
                    return new ApiRequestException(PERFUME_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        perfumeRepository.delete(perfume);
        logger.info("Perfume with id {} deleted successfully", perfumeId);
        return "Perfume deleted successfully";
    }

    @Override
    public DataFetcher<Perfume> getPerfumeByQuery() {
        return dataFetchingEnvironment -> {
            String perfumeIdStr = dataFetchingEnvironment.getArgument("id");
            if (perfumeIdStr == null) {
                logger.warn("GraphQL query: perfume id is null");
                return null; // Or throw an appropriate GraphQL exception
            }
            Long perfumeId = Long.parseLong(perfumeIdStr);
            logger.info("GraphQL query: fetching perfume by id {}", perfumeId);
            return perfumeRepository.findById(perfumeId)
                    .orElse(null); // Or throw an appropriate GraphQL exception if not found
        };
    }

    @Override
    public DataFetcher<List<PerfumeProjection>> getAllPerfumesByQuery() {
        return dataFetchingEnvironment -> {
            logger.info("GraphQL query: fetching all perfumes");
            return perfumeRepository.findAllByOrderByIdAsc();
        };
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByIdsQuery() {
        return dataFetchingEnvironment -> {
            List<String> objects = dataFetchingEnvironment.getArgument("ids");
            if (objects == null) {
                logger.warn("GraphQL query: perfume ids list is null");
                return List.of(); // Or throw an appropriate GraphQL exception
            }
            List<Long> perfumesId = objects.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            logger.info("GraphQL query: fetching perfumes by ids {}", perfumesId);
            return perfumeRepository.findByIdIn(perfumesId);
        };
    }
}
