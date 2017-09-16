---
--- Created by Pengjinfei.
--- DateTime: 8/31/17 20:58
---
local res={};
local hKey=KEYS[1]
local max=tonumber(KEYS[2]);
local updateLen=tonumber(KEYS[3]);
local delLen=tonumber(KEYS[4]);
for i = 1, updateLen do
    local val = redis.call("hincrby", hKey, ARGV[i], 1)
    if val>=max then
        table.insert(res, ARGV[i])
        redis.call("hdel",hKey,ARGV[i])
    end
end
for i = 1, delLen do
    redis.call("hdel",hKey,ARGV[updateLen+i])
end
return res;
