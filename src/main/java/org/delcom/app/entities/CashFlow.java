package org.delcom.app.entities;

import jakarta.persistence.*; // PENTING: Pakai jakarta, bukan javax
import java.time.LocalDate;

@Entity
@Table(name = "cash_flows")
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String type; // INCOME atau EXPENSE

    @Column(nullable = false)
    private LocalDate date;

    public CashFlow() {}

    public CashFlow(String description, Double amount, String type, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}