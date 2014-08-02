library ieee;
use ieee.std_logic_1164.all;

entity switch is
    generic (init : std_logic);
    port (toggle : in std_logic;
        output : out std_logic := init);
end;

architecture behavioral of switch is
    signal s_output : std_logic := init;
begin
    process (toggle)
    begin
        if toggle'event and toggle = '1' then
            s_output <= not s_output;
            output <= not s_output;
        end if;
    end process ;
end;

