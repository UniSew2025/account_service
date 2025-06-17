package com.unisew.account_service.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`wallet`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`account_id`")
    Integer id;

    long balance;

    @Column(name = "`pending_balance`")
    long pendingBalance;

    @Column(name = "`card_number`")
    String cardNumber;

    @Column(name = "`card_name`")
    String cardName;

    @Column(name = "`card_expired_date`")
    String cardExpiredDate;

    int cvv;

    @OneToOne
    @MapsId
    @JoinColumn(name = "`account_id`")
    Account account;

    @OneToMany(mappedBy = "wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> transactions;
}
