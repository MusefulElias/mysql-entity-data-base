# mysql-entity-data-base
用于orm映射到MySQL数据库的小项目，初步开发，各方面有待提升。
# 使用说明


------



### 1.设置配置类

在src/main/java任意文件夹下创建一个如下所示的配置类，用于对MySQL数据库的连接：

```java
@MyconConfiguration
public class MysqlConfig extends SqlConfig {
    private MysqlConfig() {
        url = "xxx";
        user = "xxx";
        passwd = "xxx";
        //可设置通过父类继承下来的字段修改默认数据类型转换
    }
}
```

注：须继承自SqlConfig类，同时标上注解@MyconConfiguration。

### 2.准备JavaBean类

在src/main/java任意文件夹下创建多个JavaBean文件用于映射：

```java
@Eobject
@Data
public class goods {
    @TypeOf("int")
    private int id;
    @TypeOf("int")
    private int price;
}
```

注1：使用注解@Eobject表明为映射类

注2：使用@TypeOf对转换类型指定，如果不使用则进行默认转换，在配置类中设置默认转换，原始默认有误，使用默认前最好自行在配置类修改。

注3：推荐Lombok的@Data方法来设置，或者自行书写时记得包含对equals方法的重写。

注4：不会映射静态类型字段，同时可以利用这个特性在类中去自定义一些与静态字段有关方法

### 3.@PrimaryKey和@ForeignKey

正如注解名字那般，是为了给字段标注为主键和外键使用的。

目前功能尚在完善中。

注：使用@PrimaryKey，目前还是可以映射成主键，仅对第一个使用有效。

### 4.EntityStore初始化

tryGet(	)	：尝试去获取唯一的EntityStore。

setOverWrite(boolean	status)	：设置是否覆盖原数据库的表结构，默认为false。

setShowSql(	)	：设置控制台是否显示输出执行的sql语句。

```java
EntityStore entityStore = EntityStore.tryGet();
EntityStore.setOverWrite(true);
EntityStore.setShowSql(true);
```

### 5.获取对应Entity对象

虽然有返回值，但最好采用链式编程，直接调用方法。

```java
entityStore.getEntityMap(Animal.class);
```

方法参考：

```java
	public abstract Entity<E> add(E e);
	public abstract Entity<E> add(Collection<E> es);
	
	public abstract Entity<E> delete();
    	public abstract Entity<E> delete(E e);
    	public abstract Entity<E> delete(Collection<E> es);

    	public abstract Entity<E> update(E e);
    	public abstract Entity<E> update(Collection<E> es);
    	public abstract Entity<E> update(Consumer<E> consumer);

    	public abstract Entity<E> select();
    	public abstract Entity<E> select(Class<?> clazz);
    	public abstract Entity<E> select(E e);
    	public abstract Entity<E> select(Predicate<E> predicate);

    	public abstract Entity<E> foreachDo(EntityForeachDo<E> entityForeachDo);
	//void (entity, object)->{entity.delete(object)}

    	public abstract Collection<E> toCollection();
    	public abstract Entity<E> save();
```

在修改后，使用save(	)方法才会写入数据库。

注：虽然不会一直保持连接，但是增删改时依然会在方法执行后短暂连接。
