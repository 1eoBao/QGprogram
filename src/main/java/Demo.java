import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.User;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class Demo {

    //加载mybatis配置文件，并获取SqlSessionFactory对象
    public static void main(String[] args) throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //获取SqlSessionFactory对象后，执行sql语句
        SqlSession sqlSession = sqlSessionFactory.openSession();


        //执行sql语句
        List<User> user = sqlSession.selectList("test.SelectAll");

        //打印结果

        System.out.println(user);
        //关闭SqlSession
        sqlSession.close();


    }
}
