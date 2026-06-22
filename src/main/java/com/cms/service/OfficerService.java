package com.cms.service;

import com.cms.dto.IncidentOfficerStatJpqlDto;
import com.cms.dto.OfficerIncidentStatRespDto;
import com.cms.dto.OfficerResponseDto;
import com.cms.dto.OfficerStationDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.mapper.OfficerMapper;
import com.cms.model.Officer;
import com.cms.repository.OfficerRepository;
import com.cms.utility.FileUtility;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@AllArgsConstructor
public class OfficerService {

    private OfficerRepository officerRepository;
    private OfficerMapper officerMapper;

    private static final String UPLOAD_LOC = "D:/hexaware/file upload api";
    public Officer getOfficerById(int officerId) {
        return officerRepository.findById(officerId).orElseThrow(()->new ResourceNotFoundException("Invalid officer Id"));

    }

    public Officer getByUsername(String officerUsername) {
        return officerRepository.findByUserUsername(officerUsername);
    }

    public List<OfficerResponseDto> getOfficersByStationHead(String stationHeadUsername) {

        List<Officer> officers =
                officerRepository.findByStationStationHeadUserUsername(stationHeadUsername);

        return officers.stream()
                .map(officerMapper::mapEntityToDto)
                .toList();
    }

    public List<OfficerStationDto> getOfficersByStationTitle(String stationTitle) {
        List<Officer> list=officerRepository.findByStationStationTitle(stationTitle);
        return list
                .stream()
                .map(officerMapper::mapDtoForEntity)
                .toList();
    }

    public OfficerIncidentStatRespDto incidentByOfficerStat() {
        List<IncidentOfficerStatJpqlDto> list =  officerRepository.incidentByOfficerStat();

        //  Convert List<IncidentOfficerStatJpqlDto> to OfficerIncidentStatRespDto

        List<String> names =  list.stream()
                .map(IncidentOfficerStatJpqlDto::name)
                .toList();

        List<Long> numberList =  list.stream()
                .map(IncidentOfficerStatJpqlDto :: numberOfIncidents)
                .toList();

        return new OfficerIncidentStatRespDto(
                "Incidents per Officer Stat",
                names,
                numberList
        );
    }

    public void upload(String username, MultipartFile file) throws IOException {
        // Fetch Officer by given username
        Officer officer = getByUsername(username);
        // this officer coming from DB does not have the ID path attached to it


        FileUtility.validateFile(file);

        // Original filename
        String fileName = file.getOriginalFilename();

        // i am creating the path where i will upload the file: destination
        Path uploadPath =  Paths.get(UPLOAD_LOC).normalize();
        // Attach the file name to the upload path
        Path destinationPath =  uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename())).normalize();


        // Copy the original file (Multipart) on to destination upload path
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Save the file name in db
        officer.setIdPath(fileName);

        officerRepository.save(officer);
    }

    /*public Officer getByIncidentId(int incidentId) {
        return officerRepository.getByIncidentId(incidentId);
    }*/
}
