package org.example.dscommerce.repositories

import org.example.dscommerce.entities.User
import org.example.dscommerce.projections.UserDetailsProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT tb_user.name, 
                   tb_user.email AS username, 
                   tb_user.phone, 
                   tb_user.birth_date AS birthDate, 
                   tb_user.password, 
                   tb_role.id AS roleId, 
                   tb_role.authority
            FROM tb_user
            INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
            INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
            WHERE tb_user.email = :email
        """
    )
    fun findByEmail(@Param("email") email: String): List<UserDetailsProjection>
}