library ieee;
use ieee.std_logic_1164.all;

entity many_ports_outer is
    port (signal ctrl : in std_logic_vector(1 downto 0);
        signal in0 : in std_logic;
        signal out0 : out std_logic);
end;

architecture behavioral of many_ports_outer is
    signal output : std_logic;
    signal input : std_logic;
begin
    input <= '1';
    many_ports :
    entity work.many_ports(behavioral)
        port map(in0 => in0, in1 => in0, in2 => in0, in3 => in0, in4 => in0,
            in5 => in0, in6 => in0, in7 => in0, in8 => in0, in9 => in0, in10 =>
            in0, in11 => in0, in12 => in0, in13 => in0, in14 => in0, in15 =>
            in0, in16 => in0, in17 => in0, in18 => in0, in19 => in0, in20 =>
            in0, in21 => in0, in22 => in0, in23 => in0, in24 => in0, in25 =>
            in0, in26 => in0, extra_in => input, extra_out => out0);
    output <= out0 when ctrl = "10" else
    '0' when ctrl = "01" else
    '1';
end;

