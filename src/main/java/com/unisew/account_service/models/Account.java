package com.unisew.account_service.models;

import com.unisew.account_service.enums.RegisterType;
import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`account`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String email;

    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "`register_type`")
    RegisterType registerType;

    @Column(name = "`register_date`")
    LocalDate registerDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Wallet wallet;

    @OneToMany(mappedBy = "sender")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> senderTransactions;

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> receiverTransactions;
}
