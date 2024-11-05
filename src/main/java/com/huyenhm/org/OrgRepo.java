package com.huyenhm.org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepo extends JpaRepository<Org, Long> {

}
