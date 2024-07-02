package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.SetOfBooks;

import java.util.List;

public interface SetOfBooksMapper extends Mapper<SetOfBooks> {

    List<SetOfBooks> baseSelect(SetOfBooks setOfBooks);

    List<SetOfBooks> selectLovForCompany(SetOfBooks setOfBooks);

}