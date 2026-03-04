package com.datavet.datavet.pet.testutil;

import com.datavet.datavet.pet.domain.model.OwnerInfo;
import com.datavet.datavet.pet.domain.model.Pet;
import com.datavet.datavet.pet.domain.model.Sex;
import com.datavet.datavet.shared.domain.valueobject.Phone;

import java.time.LocalDate;

public class PetTestDataBuilder {

    private static final String DEFAULT_CLINIC_ID = "Clinic_id";
    private static final String DEFAULT_PET_NAME = "Zeus";
    private static final String DEFAULT_PET_SPECIE = "Perro";
    private static final String DEFAULT_PET_BREED = "Golden retriver";
    private static final Sex DEFAULT_PET_SEX = Sex.MALE;
    private static final LocalDate DEFAULT_PET_DATE_OF_BIRTH = LocalDate.of(2020,04,05);
    private static final String DEFAULT_PET_CHIP_NUMBER = "Numero_de_chip";
    private static final String DEFAULT_PET_AVATAR = "Esto_seria_un_link";
    private static final OwnerInfo DEFAULT_PET_OWNER_INFO = aValidOwnerInfo();

    private static final String DEFAULT_OWNER_NAME = "Eddy";
    private static final String DEFAULT_OWNER_LAST_NAME = "Murfi";
    private static final String DEFAULT_OWNER_PHONE = "+34123456789";

    /* Create a valid OwnerInfo with default test data */
    public static OwnerInfo aValidOwnerInfo(){
        return OwnerInfo.from(DEFAULT_OWNER_NAME, DEFAULT_OWNER_LAST_NAME, aValidPhone());
    }

    /* Create a valid OwnerInfo without name */
    public static OwnerInfo anOwnerInfoWithName(String name) {
        return OwnerInfo.from(name, DEFAULT_OWNER_LAST_NAME, aValidPhone());
    }

    /* Create a valid OwnerInfo without phone*/
    public static OwnerInfo anOwnerInfoWithPhone(Phone phone) {
        return OwnerInfo.from(DEFAULT_OWNER_NAME, DEFAULT_OWNER_LAST_NAME, phone);
    }

    /* PET - PET - PET - PET - PET - PET - PET - PET - PET*/

    /* Create a valid Pet using the factory method with default test data */
    public static Pet aValidPet() {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with ClinicId */
    public static Pet aPetWithCLinicId(String clinicId) {
        return Pet.create(
                clinicId,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with name*/
    public static Pet aPetWithName(String name) {
        return Pet.create(DEFAULT_CLINIC_ID,
                name,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with specie*/
    public static Pet aPetWithSpecie(String specie) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                specie,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with breed*/
    public static Pet aPetWithBreed(String breed) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                breed,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with Sex*/
    public static Pet aPetWithSex(Sex sex) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                sex,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with birthdate*/
    public static Pet aPetWithBirthdate(LocalDate birthdate) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                birthdate,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet with ChipNumber*/
    public static Pet aPetWithChip(String chip) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                chip,
                DEFAULT_PET_AVATAR,
                DEFAULT_PET_OWNER_INFO);
    }

    /* Create a valid Pet without Owner */
    public static Pet aPetWithoutOwner() {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                null);
    }

    /* Create a valid Pet with Owner */
    public static Pet aPetWithOwner(OwnerInfo ownerInfo) {
        return Pet.create(DEFAULT_CLINIC_ID,
                DEFAULT_PET_NAME,
                DEFAULT_PET_SPECIE,
                DEFAULT_PET_BREED,
                DEFAULT_PET_SEX,
                DEFAULT_PET_DATE_OF_BIRTH,
                DEFAULT_PET_CHIP_NUMBER,
                DEFAULT_PET_AVATAR,
                ownerInfo);
    }

    /* UTIL - UTIL - UTIL - UTIL - UTIL - UTIL - UTIL - UTIL - UTIL - UTIL */

    public static Phone aValidPhone() {
        return new Phone(DEFAULT_OWNER_PHONE);
    };

}
