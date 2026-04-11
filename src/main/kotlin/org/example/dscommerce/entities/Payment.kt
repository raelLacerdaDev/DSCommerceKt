package org.example.dscommerce.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Entity
@Table(name = "tb_payment")
class Payment @OptIn(ExperimentalTime::class) constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    val moment: Instant,

    @OneToOne
    @MapsId
    val order: Order
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Payment) return false
        return other.id == this.id
    }
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}