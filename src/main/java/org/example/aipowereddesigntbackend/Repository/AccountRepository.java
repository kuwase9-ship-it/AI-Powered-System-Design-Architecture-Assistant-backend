package org.example.aipowereddesigntbackend.Repository;

import org.example.aipowereddesigntbackend.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Account findAccountByUserName(String username);

  Optional<Account> findByProvider(String provider);
}
