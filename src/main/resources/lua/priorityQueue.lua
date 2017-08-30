local prefix=KEYS[1];
local size=tonumber(KEYS[2]);
local res={};
local cur=0;
local originkeys={};
local len=string.len(prefix);
for skey,key in ipairs(redis.call("keys",prefix.."*")) do
    table.insert(originkeys,tonumber(string.sub(key, len+1, string.len(key))));
end
table.sort(originkeys);
for key,value in ipairs(originkeys) do
    local realKey=prefix..value;
    while(cur<size) do
        local val=redis.call("rpop",realKey)
        if(val==false) then break else
            cur=cur+1;
            table.insert(res, val)
        end
    end
    if(cur==size) then break end
end
return res;
