package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.SetOfBooks;
import com.hand.hls.gld.mapper.SetOfBooksMapper;
import com.hand.hls.gld.service.ISetOfBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SetOfBooksServiceImpl extends BaseServiceImpl<SetOfBooks> implements ISetOfBooksService{

    @Autowired
    private SetOfBooksMapper booksMapper;

    @Override
    public List<SetOfBooks> baseSelect(IRequest request, SetOfBooks setOfBooks, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return booksMapper.baseSelect(setOfBooks);
    }

    @Override
    public List<SetOfBooks> selectLovForCompany(IRequest request, SetOfBooks setOfBooks, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return booksMapper.selectLovForCompany(setOfBooks);
    }
}