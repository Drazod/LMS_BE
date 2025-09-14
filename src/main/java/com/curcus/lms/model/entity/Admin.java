package com.curcus.lms.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@Entity
@DiscriminatorValue(value = UserRole.Role.ADMIN)
@Table(name = "admins") //
public class Admin extends User {

}
