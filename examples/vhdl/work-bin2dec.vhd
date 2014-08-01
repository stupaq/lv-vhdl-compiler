library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity bin2dec is
    port (input : in std_logic_vector(15 downto 0);
        output : out std_logic_vector(19 downto 0));
end;

architecture behavioral of bin2dec is
    type temp_array is array (16 downto 0) of std_logic_vector(36 downto 0);
    signal temp : temp_array;
begin
    layer_for : for i in 0 to 15 generate
        add3_for : for j in 0 to 4 generate
            add3 :
            entity work.Add3
                port map(temp(i) (15 + 4 * (j + 1) downto 16 + 4 * j), temp(i +
                    1) (16 + 4 * (j + 1) downto 17 + 4 * j));
        end generate add3_for;
        temp(i + 1) (16 downto 1) <= temp(i) (15 downto 0);
        temp(i + 1) (0) <= '0';
    end generate layer_for;
    output <= temp(16) (35 downto 16);
    temp(0) (36 downto 16) <= (others => '0');
    temp(0) (15 downto 0) <= input;
end;

