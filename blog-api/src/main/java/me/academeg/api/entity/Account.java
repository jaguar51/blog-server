package me.academeg.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * Account Entity
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Setter
@Getter

@Entity
@Table(name = "account")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Account {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Type(type = "uuid-char")
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String login;

    @Column
    private String name;

    @Column
    private String surname;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @Size(min = 4, max = 255)
    @NotBlank
    private String password;

    @OneToOne(mappedBy = "account")
    private Avatar avatar;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Article> articles;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @JsonIgnore
    private String authority;

    public Account() {
    }

    public Account(UUID uuid) {
        this.id = uuid;
    }
}
