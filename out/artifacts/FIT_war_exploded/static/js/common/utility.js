function RetainedDecimalPlaces(num, del) //值：num 小数位：del
{
    if (del != 0)
    {
        num = parseFloat(num).toFixed(del); //天花板函数保留小数并四舍五入
    }
    var source = String(num).split(".");//按小数点分成2部分
    source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
    return source.join(".");//再将小数部分合并进来
};