package app.security;

import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    private static final String INACTIVE_PROFILE_REDIRECT_MESSAGE = "Your profile is not active";
    public static final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/login", "/register", "/");

    private final UserService userService;

    @Autowired
    public SessionCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //потребителят иска да достъпи разрешена страница
        if(UNAUTHENTICATED_ENDPOINTS.contains(request.getServletPath())){
            return true;
        }

        //ако потребителят няма сесия се препраша лъм логин страницата
        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendRedirect("/login");
            return false;
        }

        //ако в сесията няма атрибут с такова име сесията се зачиства и се препраща отново към логин
        Object userId = session.getAttribute("userId");
        if(userId == null){
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }

        User user = userService.getById((UUID) userId);
        if(!user.isActive()){
            session.invalidate();
            response.sendRedirect("/login?loginAttemptMessage=" + INACTIVE_PROFILE_REDIRECT_MESSAGE);
            return false;
        }

        return true;
    }
}
