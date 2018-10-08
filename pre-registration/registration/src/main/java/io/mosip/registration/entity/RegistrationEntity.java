package io.mosip.registration.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The Class RegistrationStatusEntity.
 */
@Component
@Entity
@Table(name = "applicant_demographic", schema = "prereg")
@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
public class RegistrationEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The pre registration id. */
	@Column(name = "prereg_id", nullable = false)
	@Id
	private String preRegistrationId;
	
	/** The group id. */
	@Column(name = "group_id", nullable = false)
	private String groupId;

	/** The firstname */
	@Column(name = "firstname")
	private String firstname;
	
	/** The firstname */
	@Column(name = "forename")
	private String forename;	
	
	/** The givenname */
	@Column(name = "givenname")
	private String givenname;
	
	/** The middlename */
	@Column(name = "middlename")
	private String middlename;
	
	/** The middleinitial */
	@Column(name = "middleinitial")
	private String middleinitial;
	
	/** The lastname */
	@Column(name = "lastname")
	private String lastname;
	
	
	/** The surname */
	@Column(name = "surname")
	private String surname;
	
	/** The familyname */
	@Column(name = "familyname")
	private String familyname;
	
	/** The fullname */
	@Column(name = "fullname")
	private String fullname;
	

	/** The genderCode */
	@Column(name = "gender_code", nullable=false)
	private String genderCode;
	
	
	/** The parentFullname */
	@Column(name = "parent_fullname")
	private String parentFullName;
	
	/** The parent_ref_id_type */
	@Column(name = "parent_ref_id_type")
	private String parentRefIdType;
	
	/** The parent_ref_id */
	@Column(name = "parent_ref_id")
	private String parentRefId;
	
	/** The dob */
	@Column(name = "dob")
	private Date dob;
	
	/** The age */
	@Column(name = "age")
	private int age;
	
	/** The addr_line1 */
	@Column(name = "addr_line1")
	private String addrLine1;
	
	/** The addr_line2 */
	@Column(name = "addr_line2")
	private String addrLine2;
	
	/** The addr_line3 */
	@Column(name = "addr_line3")
	private String addrLine3;
	
	/** The location_code */
	@Column(name = "location_code", nullable=false)
	private String locationCode;
	
	/** The mobile */
	@Column(name = "mobile")
	private String mobile;
	
	/** The email */
	@Column(name = "email")
	private String email;
	
	/** The applicant_type */
	@Column(name = "applicant_type", nullable=false)
	private String applicantType;
	
	/** The nationalid */
	@Column(name = "nationalid")
	private String nationalid;
	
	/** The status_code */
	@Column(name = "status_code", nullable=false)
	private String statusCode;
	
	/** The lang_code */
	@Column(name = "lang_code", nullable=false)
	private String langCode;
	
	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The create date time. */
	@Column(name = "cr_dtimesz")
	@CreationTimestamp
	private LocalDateTime createDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The update date time. */
	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updateDateTime;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime deletedDateTime;

	@Override
	public String toString() {
		return "RegistrationEntity [preRegistrationId=" + preRegistrationId + ", groupId=" + groupId + ", firstname="
				+ firstname + ", forename=" + forename + ", givenname=" + givenname + ", middlename=" + middlename
				+ ", middleinitial=" + middleinitial + ", lastname=" + lastname + ", surname=" + surname
				+ ", familyname=" + familyname + ", fullname=" + fullname + ", genderCode=" + genderCode
				+ ", parentFullName=" + parentFullName + ", parentRefIdType=" + parentRefIdType + ", parentRefId="
				+ parentRefId + ", dob=" + dob + ", age=" + age + ", addrLine1=" + addrLine1 + ", addrLine2="
				+ addrLine2 + ", addrLine3=" + addrLine3 + ", locationCode=" + locationCode + ", mobile=" + mobile
				+ ", email=" + email + ", applicantType=" + applicantType + ", nationalid=" + nationalid
				+ ", statusCode=" + statusCode + ", langCode=" + langCode + ", createdBy=" + createdBy
				+ ", createDateTime=" + createDateTime + ", updatedBy=" + updatedBy + ", updateDateTime="
				+ updateDateTime + ", isDeleted=" + isDeleted + ", deletedDateTime=" + deletedDateTime + "]";
	}
	
	
	

}
