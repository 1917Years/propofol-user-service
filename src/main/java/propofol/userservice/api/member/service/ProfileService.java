package propofol.userservice.api.member.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.common.exception.SaveProfileException;
import propofol.userservice.api.common.properties.ProfilePropertiees;
import propofol.userservice.api.member.controller.dto.ProfileResponseDto;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.image.repository.ProfileRepository;
import propofol.userservice.domain.member.entity.Member;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfilePropertiees profilePropertiees;

    @Transactional
    public ProfileResponseDto saveProfile(MultipartFile file, Member member) throws Exception{
        String profileDirPath = createProfileDir();
        String extType = getExt(file.getOriginalFilename());
        String storeFileName = createStoreFileName(extType);
        try {
            file.transferTo(new File(getFullPath(profileDirPath, storeFileName)));
        }catch (IOException e){
            throw new SaveProfileException("프로필 저장 오류");
        }

        Profile profile = null;
        Profile findProfile = profileRepository.findByMemberId(member.getId()).orElse(null);
        try {
            if (findProfile != null) {
                return getUpdateProfileResponseDto(file, member, profileDirPath, storeFileName, findProfile);
            } else {
                return getProfileResponseDto(file, member, storeFileName);
            }
        }catch (Exception e){
            throw new Exception("파일 저장 오류");
        }
    }

    private ProfileResponseDto getProfileResponseDto(MultipartFile file, Member member, String storeFileName) {
        Profile profile;
        profile = Profile.createProfile()
                .uploadFileName(file.getOriginalFilename())
                .storeFileName(storeFileName)
                .contentType(file.getContentType())
                .build();
        profile.changeMember(member);

        profileRepository.save(profile);

        return new ProfileResponseDto(member.getId(),
                getProfileByte(profile.getStoreFileName()), profile.getContentType());
    }

    private ProfileResponseDto getUpdateProfileResponseDto(MultipartFile file, Member member, String profileDirPath, String storeFileName, Profile findProfile) {
        File findFile = new File(getFullPath(profileDirPath, findProfile.getStoreFileName()));
        if(!findFile.exists()) findFile.delete();
        findProfile.updateProfile(file.getOriginalFilename(), storeFileName, file.getContentType());
        return new ProfileResponseDto(member.getId(),
                getProfileByte(findProfile.getStoreFileName()), findProfile.getContentType());
    }

    private byte[] getProfileByte(String fileName){
        String path = getFullPath(getProfileDirPath(), fileName);
        byte[] bytes = null;

        try {
            InputStream imageStream = new FileInputStream(path);
            bytes = IOUtils.toByteArray(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    private String getFullPath(String profileDirPath, String storeFileName) {
        return profileDirPath + "/" + storeFileName;
    }

    private String createStoreFileName(String extType) {
        return UUID.randomUUID().toString() + "." + extType;
    }

    private String getExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String createProfileDir(){
        String path = getProfileDirPath();
        File dir = new File(path);
        if(!dir.exists()) dir.mkdir();

        return path;
    }

    private String getProfileDirPath(){
        String profileDir = profilePropertiees.getProfileDir();
        Path relativePath = Paths.get("");
        return relativePath.toAbsolutePath() + "/" + profileDir;
    }

    public ProfileResponseDto getProfile(Long memberId) {
        Profile profile = profileRepository.findByMemberId(memberId).orElse(null);

        if(profile != null)
            return new ProfileResponseDto(memberId, getProfileByte(profile.getStoreFileName()), profile.getContentType());
        else
            return new ProfileResponseDto(memberId,  null, null);
    }
}
