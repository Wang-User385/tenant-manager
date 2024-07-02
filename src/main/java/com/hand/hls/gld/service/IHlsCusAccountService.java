package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.HlsCusAccount;

import java.util.List;

public interface IHlsCusAccountService extends IBaseService<HlsCusAccount>, ProxySelf<IHlsCusAccountService>{

    List<HlsCusAccount> queryAccountCode();

    List<HlsCusAccount> queryModify(IRequest var1, HlsCusAccount var2, int var3, int var4);
}