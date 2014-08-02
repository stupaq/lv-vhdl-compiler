library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library ieee_proposed;
use ieee_proposed.fixed_pkg.all;

library work;
use work.the_library.all;

entity air2 is
    generic (param_len : integer := 16;
             width_in : integer := 8;
             width_out : integer := 8);
    port (signal reset : in std_logic;
        signal clk : in std_logic;
        signal params : air2_params;
        signal x0 : in sfixed(width_in - 1 downto 0);
        signal y0 : out sfixed(width_out - 1 downto 0));
end;

architecture behavioral of air2 is
    constant c_high : integer := -1;
    constant c_low : integer := c_high - param_len + 1;
    constant b0_high : integer := 1;
    constant b0_low : integer := b0_high - param_len + 1;
    constant d_high : integer := c_high;
    constant d_low : integer := d_high - param_len + 1;
    signal c : sfixed(c_high downto c_low);
    signal b0 : sfixed(b0_high downto b0_low);
    signal d : sfixed(d_high downto d_low);
    constant acc_high : integer := width_out - 1;
    constant acc_low : integer := c_low;
    signal x1 : sfixed(width_in - 1 downto 0);
    signal y1 : sfixed(acc_high downto acc_low);
    constant mid_high : integer := width_out;
    constant mid_low : integer := c_low;
    type mid_t is array(0 to 2) of sfixed(mid_high downto mid_low);
    signal mid : mid_t;
    signal prod0 : sfixed(mid_high downto mid_low);
    signal prod : sfixed(mid_high downto mid_low);
    signal sum : sfixed(mid_high downto mid_low);
begin
    c <= params(0);
    b0 <= params(1);
    d <= params(2);
    process (reset, clk)
    begin
        if reset = '1' then
            x1 <= x0;
            y1 <= (others => '0');
        elsif clk'event and clk = '1' then
            x1 <= x0;
            y1 <= resize(sum, acc_high, acc_low);
        end if;
    end process;
    mid(0) <= resize(c * (y1 - x1), mid_high, mid_low);
    mid(1) <= resize(b0 * (x0 - x1), mid_high, mid_low);
    mid(2) <= resize(d * y1, mid_high, mid_low);
    sum <= resize(y1 + mid(0) + mid(1) + mid(2), mid_high, mid_low);
    y0 <= resize(sum, width_out - 1, 0);
end;

