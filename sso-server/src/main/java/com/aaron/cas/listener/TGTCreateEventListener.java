
package com.aaron.cas.listener;


import com.aaron.cas.service.IUserIdObtainService;
import com.aaron.cas.service.TriggerLogoutService;
import org.apereo.cas.support.events.ticket.CasTicketGrantingTicketCreatedEvent;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TGT创建监听（用户登录成功后对该用户的其他TGT进行剔除）
 *  1. 监听tgt创建事件
 *  2. 获取用户id，以及tgt
 *  3. 根据用户id，认证方式clientName寻找所有的tgt
 *  4. 过滤非当前用户的tgt的所有tgt
 *  5. 删除过滤后的tgt（正确的逻辑过滤后一般情况剩下一个，因为已经单用户登录了）
 *
 * @author Aaron
 * @date 2020/9/13
 */
public class TGTCreateEventListener {
    private TriggerLogoutService logoutService;
    private IUserIdObtainService service;

    public TGTCreateEventListener(@NotNull TriggerLogoutService logoutService, @NotNull IUserIdObtainService service) {
        this.logoutService = logoutService;
        this.service = service;
    }

    @EventListener
    @Async
    public void onTgtCreateEvent(CasTicketGrantingTicketCreatedEvent event) {
        TicketGrantingTicket ticketGrantingTicket = event.getTicketGrantingTicket();
        // 用户id
        String id = ticketGrantingTicket.getAuthentication().getPrincipal().getId();
        String tgt = ticketGrantingTicket.getId();
        /**
         * 为什么要采用clientName进行过滤呢，因为认证平台可能通过restful认证，
         * qq、github、微信的OAuth2认证等等，所以认证方式不同，
         * 最后的用户id以及clientName会不同，所以要根据用户认证方式以及id，
         * 找到所有该用户的认证方式进行删除tgt，
         * 否则会出现，oauth2登录的用户用账号登录无法强制注销
         */
        String clientName = (String) ticketGrantingTicket.getAuthentication().getAttributes().get("clientName");
        //获取可以认证的id
        List<String> authIds = service.obtain(clientName, id);
        if (authIds != null) {
            //循环触发登出
            authIds.forEach(authId -> logoutService.triggerLogout(authId, tgt));
        }
    }
}
