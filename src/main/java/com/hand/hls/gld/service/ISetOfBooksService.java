package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.SetOfBooks;

import java.util.List;

public interface ISetOfBooksService extends IBaseService<SetOfBooks>, ProxySelf<ISetOfBooksService> {

    List<SetOfBooks> baseSelect(IRequest request, SetOfBooks setOfBooks, int page, int pageSize);

    List<SetOfBooks> selectLovForCompany(IRequest request, SetOfBooks setOfBooks, int page, int pageSize);

}