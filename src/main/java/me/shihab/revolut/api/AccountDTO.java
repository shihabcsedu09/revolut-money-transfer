package me.shihab.revolut.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountDTO {

    @JsonProperty
    private long id;

    @NotNull
    @NotEmpty
    @JsonProperty
    private String name;

    @NotNull
    @JsonProperty
    @DecimalMin(value = "0.0")
    private BigDecimal amount;


    public AccountDTO() {
    }

    public AccountDTO(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public AccountDTO(long id, String name, BigDecimal amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
