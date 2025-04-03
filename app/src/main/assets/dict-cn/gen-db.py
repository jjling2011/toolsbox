import json
import sqlite3
from pathlib import Path

def json_to_sqlite(json_file, db_file, table_name):
    # 读取 JSON 文件
    with open(json_file, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # 检查数据是否非空
    if not data:
        print("JSON 文件为空")
        return
    
    # 获取列名（使用第一个元素的键）
    columns = list(data[0].keys())
    
    # 连接 SQLite 数据库
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    
    # 创建表（如果已存在则先删除）
    cursor.execute(f"DROP TABLE IF EXISTS {table_name}")
    
    # 构建 CREATE TABLE 语句
    create_table_sql = f"""
    CREATE TABLE {table_name} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        {', '.join(f'"{col}" TEXT' for col in columns)}
    )
    """
    cursor.execute(create_table_sql)
    
    # 插入数据
    for item in data:
        placeholders = ', '.join(['?'] * len(columns))
        insert_sql = f"""
        INSERT INTO {table_name} ({', '.join(f'"{col}"' for col in columns)})
        VALUES ({placeholders})
        """
        cursor.execute(insert_sql, [item[col] for col in columns])
    
    # 提交更改并关闭连接
    conn.commit()
    conn.close()
    
    print(f"成功将 {json_file} 转换为 {db_file} 中的 {table_name} 表")

if __name__ == "__main__":
    table_name = "word"
    json_file = "../data/"+ table_name +".json"
    db_file = "dict2.db"
    
    # 检查 JSON 文件是否存在
    if not Path(json_file).exists():
        print(f"错误：文件 {json_file} 不存在")
    else:
        json_to_sqlite(json_file, db_file, table_name)