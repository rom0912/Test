package com.romy.notification.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.notification.entity.NotiMsg;

@Repository
public interface NotiMsgRepository extends JpaRepository<NotiMsg, Long> {

	List<NotiMsg> findByMsgIdInOrderBySendDateDesc(Collection<Long> msgIds);
	
}
