library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity divider is
    generic (n : natural := 25;
        top : natural := 24999999);
    port (clk_in : in std_logic;
        reset : in std_logic;
        clk_out : out std_logic);
end;

architecture behavioral of divider is
    signal s_count : unsigned(n - 1 downto 0);
    signal s_clk : std_logic;
begin
    clk_out <= s_clk;
    process (clk_in, reset)
    begin
        if reset = '0' then
            s_count <= (others => '0');
            s_clk <= '0';
        elsif clk_in'event and clk_in = '1' then
            if s_count = 0 then
                s_count <= to_unsigned(top, n);
                s_clk <= not s_clk;
            else
                s_count <= s_count - 1;
            end if;
        end if;
    end process ;
end;

