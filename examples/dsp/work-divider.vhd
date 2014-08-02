library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity divider is
  generic (constant ratio : integer);
  port (signal reset : in std_logic;
        signal clk_in : in std_logic;
        signal clk_out : out std_logic);
end;

architecture behavioral of divider is
  signal s_count : integer range 0 to ratio;
begin
  clk_out <= clk_in when ratio = 1 else
             '1' when s_count = 0 else
             '0';
  process (clk_in)
  begin
    if reset = '1' then
      s_count <= 0;
    elsif clk_in'event and clk_in = '1' then
      case s_count is
        when ratio - 1 => s_count <= 0;
        when others => s_count <= s_count + 1;
      end case;
    end if;
  end process ;
end;

