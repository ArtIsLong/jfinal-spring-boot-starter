#namespace("theme")
  #sql("queryTheme")
    select * from theme t where t.title like concat('%',#para(title),'%')
  #end
  #sql("insert")
    insert into theme
  #end
#end
