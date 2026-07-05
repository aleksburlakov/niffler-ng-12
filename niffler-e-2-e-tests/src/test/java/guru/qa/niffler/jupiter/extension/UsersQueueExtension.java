package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class UsersQueueExtension implements
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(UsersQueueExtension.class);
  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
    WITH_INCOME_REQUEST_USERS.add(new StaticUser("dima", "12345", null, "barsik", null));
    WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "dima"));
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
        .toList()
        .forEach(userTypeParameter ->
            {
              Optional<StaticUser> user = Optional.empty();
              var requiredUserType = userTypeParameter.getAnnotation(UserType.class);
              StopWatch sw = StopWatch.createStarted();
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = switch (requiredUserType.value()) {
                  case UserType.Type.EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                  case UserType.Type.WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                  case UserType.Type.WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                  case UserType.Type.WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                };
              }
              Allure.getLifecycle().updateTestCase(testCase ->
                  testCase.setStart(new Date().getTime())
              );

              Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                  .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());

              user.ifPresentOrElse(
                  u -> userMap.put(requiredUserType, u),
                  () -> {
                    throw new IllegalStateException(
                        String.format("Can't find user of type '%s' after 30 sec", requiredUserType.value())
                    );
                  }
              );
            }
        );
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE).remove(context.getUniqueId(), Map.class);

    if (userMap != null) {
      for (Map.Entry<UserType, StaticUser> entry : userMap.entrySet()) {
        switch (entry.getKey().value()) {
          case UserType.Type.EMPTY -> EMPTY_USERS.add(entry.getValue());
          case UserType.Type.WITH_FRIEND -> WITH_FRIEND_USERS.add(entry.getValue());
          case UserType.Type.WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(entry.getValue());
          case UserType.Type.WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(entry.getValue());
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return ((Map<UserType, StaticUser>) extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), Map.class))
        .get(parameterContext.getParameter().getAnnotation(UserType.class));
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
      EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }
  }

  public record StaticUser(String username, String password, String friend, String incomeFriend, String outcomeFriend) {
  }
}
