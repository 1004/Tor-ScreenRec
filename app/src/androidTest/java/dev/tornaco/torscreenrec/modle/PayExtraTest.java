package dev.tornaco.torscreenrec.modle;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.junit.Test;
import org.newstand.logger.Logger;

import java.util.List;

/**
 * Created by Tornaco on 2017/7/29.
 * Licensed with Apache.
 */
public class PayExtraTest {
    @Test
    public void getNick() throws Exception {
        List<PayExtra> list = Lists
                .newArrayList(PayExtra.builder().nick("Nick").ad("Hello world!").build(),
                        PayExtra.builder().nick("Tor").ad("Hello Android!").build());
        Gson gson = new Gson();
        String content = gson.toJson(list);

        Logger.i(content);
    }

}