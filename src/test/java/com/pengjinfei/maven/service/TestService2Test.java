package com.pengjinfei.maven.service;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.pengjinfei.maven.dto.CouponSendDto;
import com.pengjinfei.maven.enu.Sender;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
public class TestService2Test {

    TestService testService = new TestService();

    @Test
    public void add() throws Exception {
        int add = testService.add(1, 2);
        Assert.assertEquals(3,add);
    }


    @Test
    public void testKryo() {
        Kryo kryo = new Kryo();
        final CouponSendDto couponSendDto = new CouponSendDto();
        couponSendDto.setCouponCode("123");
        couponSendDto.setMagic("abc");
        couponSendDto.setOrgCode("205");
        couponSendDto.setSender(Sender.EXCEL);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", 12L);
        couponSendDto.setContext(jsonObject);
        Message<CouponSendDto> message = MessageBuilder.withPayload(couponSendDto).setHeader("timee",new Date()).build();
/*        kryo.register(CouponSendDto.class, 101);
        kryo.register(GenericMessage.class, 102);*/

        MapSerializer mapSerializer = new MapSerializer();
        mapSerializer.setKeyClass(String.class,kryo.getSerializer(String.class));
        kryo.register(HashMap.class, mapSerializer);

        kryo.register(CouponSendDto.class, new Serializer<CouponSendDto>() {
            @Override
            public void write(Kryo kryo, Output output, CouponSendDto couponSendDto) {
                Serializer jsonSerializer = kryo.getDefaultSerializer(JSONObject.class);
                JSONObject context = couponSendDto.getContext();
                if (context != null) {
                    jsonSerializer.write(kryo,output, context);
                }
                output.writeString(couponSendDto.getCouponCode());
                output.writeString(couponSendDto.getMagic());
                output.writeString(couponSendDto.getOrgCode());
                Serializer senderSerializer = kryo.getDefaultSerializer(Sender.class);
                senderSerializer.write(kryo,output,couponSendDto.getSender());
            }

            @Override
            public CouponSendDto read(Kryo kryo, Input input, Class<CouponSendDto> aClass) {
                CouponSendDto dto = new CouponSendDto();
                Serializer jsonSerializer = kryo.getDefaultSerializer(JSONObject.class);
                dto.setContext((JSONObject) jsonSerializer.read(kryo,input,JSONObject.class));
                dto.setCouponCode(input.readString());
                dto.setMagic(input.readString());
                dto.setOrgCode(input.readString());
                Serializer senderSerializer = kryo.getDefaultSerializer(Sender.class);
                dto.setSender((Sender) senderSerializer.read(kryo,input,Sender.class));
                return dto;
            }
        }, 101);

        kryo.addDefaultSerializer(UUID.class,new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output output, UUID uuid) {
                output.writeString(uuid.toString());
            }

            @Override
            public UUID read(Kryo kryo, Input input, Class<UUID> aClass) {
                return UUID.fromString(input.readString());
            }
        });

        kryo.register(UUID.class, new Serializer<UUID>() {
            @Override
            public void write(Kryo kryo, Output output, UUID uuid) {
                output.writeString(uuid.toString());
            }

            @Override
            public UUID read(Kryo kryo, Input input, Class<UUID> aClass) {
                return UUID.fromString(input.readString());
            }
        }, 102);

        kryo.register(GenericMessage.class, new Serializer<GenericMessage>() {
            @Override
            public void write(Kryo kryo, Output output, GenericMessage genericMessage) {
                Object payload = genericMessage.getPayload();
                Serializer payloadSerializer = kryo.getSerializer(payload.getClass());
                payloadSerializer.write(kryo,output,payload);
                MessageHeaders headers = genericMessage.getHeaders();
                Set<Map.Entry<String, Object>> entries = headers.entrySet();
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<String, Object> entry : entries) {
                    String key = entry.getKey();
                    if(!key.equals("id")&& !key.equals("timestamp"))
                    map.put(key, entry.getValue());
                }
                kryo.writeClassAndObject(output, map);
            }

            @Override
            public GenericMessage read(Kryo kryo, Input input, Class<GenericMessage> aClass) {
                Serializer messageSerializer = kryo.getDefaultSerializer(HashMap.class);
                Serializer payloadSerializer = kryo.getSerializer(CouponSendDto.class);
                CouponSendDto dto = (CouponSendDto) payloadSerializer.read(kryo, input, CouponSendDto.class);
                HashMap<String ,Object> map = (HashMap<String ,Object>) kryo.readClassAndObject(input);
                MessageHeaders read = new MessageHeaders(map);
                return new GenericMessage(dto,read);
            }
        }, 102);

        Output outputStream = new Output(1000);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);
        outputStream.setOutputStream(byteArrayOutputStream);
        kryo.writeObject(outputStream,message);

        outputStream.flush();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        Input input = new Input(byteArrayInputStream, 1000);
        GenericMessage object = kryo.readObject(input, GenericMessage.class);
        System.out.println("good");
    }

}