library ieee;
use ieee.std_logic_1164.all;

entity tiny is
    port (clk : in std_logic;
        in1 : in std_logic;
        in2 : in std_logic;
        in3 : in integer;
        out1 : out std_logic;
        out2 : out std_logic;
        out3 : out std_logic);
end;

architecture behavioral of tiny is
    constant cons : integer := 7;
    signal s1 : std_logic;
    signal s2 : std_logic;
begin
    inst1 :
    entity work.tiny1(behavioral)
        generic map(gen1 => cons)
        port map(in1 => in1, out1 => out1);
    inst2 :
    entity work.tiny1(behavioral)
        generic map(gen1 => in3)
        port map(in1 => in1, out1 => out3);
    out2 <= s1 when in1 = '1' else
        s2;
    process (clk) is
    begin
        if clk'event and clk = '1' then
            if in1 = '1' then
                s1 <= '0';
                s2 <= not in2;
            else
                s1 <= in2;
            end if;
        end if;
    end process ;
end;

