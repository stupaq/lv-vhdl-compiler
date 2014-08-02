library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library ieee_proposed;
use ieee_proposed.fixed_pkg.all;

entity decimal_acc is
    generic (width : integer := 26;
        digits : integer := 8);
    port (rst : in std_logic;
        clk : in std_logic;
        change : in signed(1 downto 0);
        value : out unsigned(width - 1 downto 0));
end;

architecture behavioral of decimal_acc is
    constant base : integer := 10;
    signal s_exp : integer range 0 to digits;
    type exp2val_t is array (0 to 8) of ufixed(width downto 0);
    constant exp2val : exp2val_t := (to_ufixed(1, width, 0), to_ufixed(10,
        width, 0), to_ufixed(100, width, 0), to_ufixed(1000, width, 0),
        to_ufixed(10000, width, 0), to_ufixed(100000, width, 0), to_ufixed(
        1000000, width, 0), to_ufixed(10000000, width, 0), to_ufixed(100000000,
        width, 0));
    signal s_rem : integer range - 1 to base;
    type rem2val_t is array (- 1 to base) of ufixed(3 downto - 4);
    constant rem2val : rem2val_t := (to_ufixed(0, 3, - 4), to_ufixed(1, 3, - 4)
        , to_ufixed(1.2589, 3, - 4), to_ufixed(1.5849, 3, - 4), to_ufixed(
        1.9953, 3, - 4), to_ufixed(2.5119, 3, - 4), to_ufixed(3.1623, 3, - 4),
        to_ufixed(3.9811, 3, - 4), to_ufixed(5.0119, 3, - 4), to_ufixed(6.3096,
        3, - 4), to_ufixed(7.9433, 3, - 4), to_ufixed(10, 3, - 4));
begin
    process (rst, clk)
    begin
        if rst = '1' then
            s_exp <= 0;
            s_rem <= - 1;
        elsif clk'event and clk = '1' then
            if change = "01" then
                if s_rem = base - 1 then
                    if s_exp < digits then
                        s_rem <= 0;
                        s_exp <= s_exp + 1;
                    end if;
                else
                    s_rem <= s_rem + 1;
                end if;
            elsif change = "11" then
                if s_rem = 0 then
                    if s_exp > 0 then
                        s_rem <= base - 1;
                        s_exp <= s_exp - 1;
                    elsif s_rem = 0 then
                        s_rem <= - 1;
                    end if;
                elsif s_rem > 0 then
                    s_rem <= s_rem - 1;
                end if;
            end if;
        end if;
    end process ;
    value <= to_unsigned(exp2val(s_exp) * rem2val(s_rem), width);
end;

