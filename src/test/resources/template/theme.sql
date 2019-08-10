#namespace("theme")
  #sql("queryTheme")
    select * from theme t where t.title like concat('%',#para(title),'%')
  #end
#end
