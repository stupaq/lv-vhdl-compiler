library ieee;
use ieee.std_logic_1164.all;

entity debouncer is
    port (input : in std_logic;
        clk : in std_logic;
        output : out std_logic := '0');
end;

architecture counter of debouncer is
    signal s_count : natural range 1 to 1000 := 1;
    signal s_value : std_logic := '0';
begin
    output <= s_value;
    process (clk) is
    begin
        if rising_edge(clk) then
            if s_count = 1000 then
                s_value <= input;
                s_count <= 1;
            elsif s_value = input then
                s_count <= 1;
            else
                s_count <= s_count + 1;
            end if;
        end if;
    end process ;
end;

architecture behavioral of debouncer is
    signal s_prev : std_logic := '0';
begin
    process (clk) is
    begin
        if clk'event and clk = '1' then
            if input = '1' and s_prev = '1' then
                output <= '1';
            elsif input = '0' and s_prev = '0' then
                output <= '0';
            else
                s_prev <= input;
            end if;
        end if;
    end process ;
end;

