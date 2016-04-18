package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.SignatureVisible;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sqhe18 on 14-12-29.
 */
public interface SignatureVisibleRepositoty extends PagingAndSortingRepository<SignatureVisible,Long> {
    public List<SignatureVisible> findAll();
    public SignatureVisible findByProcessID(long ProcessID);
}
