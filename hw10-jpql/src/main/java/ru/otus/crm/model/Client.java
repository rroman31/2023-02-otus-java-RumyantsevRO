package ru.otus.crm.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "client")
    private List<Phone> phones = new ArrayList<>();

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addAddress(Address address) {
        this.address = address;
        this.address.setClient(this);
    }

    public void addPhone(Phone phone) {
        this.phones.add(phone);
        phone.setClient(this);
    }

    @Override
    public Client clone() {
        var copyOfClient = new Client(this.id, this.name);
        phones.forEach(phone -> cloneAndLinkWithClient(phone, copyOfClient));
        if (Objects.nonNull(address)) {
            var copyOfAddress = address.clone();
            copyOfClient.addAddress(copyOfAddress);
        }
        return copyOfClient;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    private void cloneAndLinkWithClient(Phone phone, Client client) {
        var copyOfPhone = phone.clone();
        copyOfPhone.addClient(client);
        client.phones.add(copyOfPhone);
    }
}
