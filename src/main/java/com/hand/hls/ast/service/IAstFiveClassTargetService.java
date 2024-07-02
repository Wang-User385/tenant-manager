//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.AstFiveClassTarget;
import java.util.List;

public interface IAstFiveClassTargetService extends IBaseService<AstFiveClassTarget>, ProxySelf<IAstFiveClassTargetService> {
    List<AstFiveClassTarget> queryAll(AstFiveClassTarget var1, int var2, int var3);
}
