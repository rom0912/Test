package com.romy.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.notification.entity.NotiMng;

@Repository
public interface NotiMngRepository extends JpaRepository<NotiMng, Long> {

	List<NotiMng> findByUserKeyOrderByReadYnDescCreateDateDesc(String userKey);

	NotiMng findByNotiId(Long notiId);
	
}
