package indi.midreamsheep.vegetable.backend.features.auth.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.auth.domain.port.CredentialPort;
import indi.midreamsheep.vegetable.backend.features.auth.domain.port.TokenPort;

/**
 * 认证领域服务：封装登录等认证相关的核心业务逻辑。
 */
public class AuthDomainService {

    private final CredentialPort credentialPort;
    private final TokenPort tokenPort;

    /**
     * 构造认证领域服务。
     *
     * @param credentialPort 凭证校验端口
     * @param tokenPort token 端口
     */
    public AuthDomainService(CredentialPort credentialPort, TokenPort tokenPort) {
        this.credentialPort = credentialPort;
        this.tokenPort = tokenPort;
    }

    /**
     * 登录并返回 token。
     *
     * @param username 用户名
     * @param password 密码
     * @return token 字符串
     */
    public String login(String username, String password) {
        Long userId = credentialPort.verifyAndGetUserId(username, password)
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED, "Invalid username or password"));
        return tokenPort.generate(String.valueOf(userId));
    }
}
