package welfare.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* 统一返回信息
* code应协商规范
* 常用失败返回请在CommonErr内添加，保证统一性
* */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private int code;
    private String message;
    private Object data;

    //ok:成功
    public static Response ok() {  //告知响应成功
        return new Response(200,"ok",null);
    }

    //success:成功且返回单个数据
    public static Response success(Object data) {  //返回响应参数
        return new Response(200,"success",data);
    }

    //result:返回成功与否的情况列表
    public static Response result(Object data) {  //返回多个结果
        return new Response(200,"result",data);
    }



    //failure:失败且无返回
    public static Response failure(int code, String msg) {  //告知操作失败，并返回原因
        return new Response(code, msg,null);
    }

    //常用失败返回
    public static Response failure(CommonErr commonErr) {
        return new Response(commonErr.getCode(),commonErr.getMessage(),null);
    }



    //error:出错
    public static Response error(int code, String msg) {  //告知出现错误,并返回错误
        return new Response(code,msg,null);
    }

}
