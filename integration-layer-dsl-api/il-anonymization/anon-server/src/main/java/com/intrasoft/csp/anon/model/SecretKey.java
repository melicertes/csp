package com.intrasoft.csp.anon.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "secret_key")
public class SecretKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "seckey") //key is a reserved word in mysql and it will fail if selected; thus, changing to seckey
    private String key;

    Date createdAt;

    public SecretKey() {
    }

    public SecretKey(String key, Date createdAt) {
        this.key = key;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecretKey{");
        sb.append("id=").append(id);
        sb.append(", key='").append(key).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }
}
